var lastBuildStatus = '';

function getBuildStatus()
{
    invoke('build/status.json', function(data)
		{
			if(data)
			{
                var buildStatus = data.status;
				$('#build-status').text(data.status)

				if(buildStatus != lastBuildStatus)
                {
                    displayHistory();
                }

                if(isCurrentlyBuilding(buildStatus))
                {
                    displayStats(data);
                    displayRunningJobs();
                }
                else
                {
                    clearStats();
                    clearRunningJobs();
                }

                lastBuildStatus = buildStatus;
			}
		});
}

function displayStats(data)
{
    $('#build-details').html(['<span>Total jobs: ', data.totalJobs, '</span><br/><span>Remaining jobs:', data.remainingJobs, '</span>'].join(''));
}

function displayHistory()
{
    invoke('build/history.json', function(data)
		{
			if(data)
			{
			    var graphData = [];
			    var jobRunTimes = [];
			    var html = [];
			    for(var i = 0, n = data.length; i < n; i++)
			    {
			        var startTimestamp = data[i].startTimestamp;
			        var durationMillis = data[i].endTimestamp - startTimestamp;
			        var durationElements = toTimeElements(durationMillis);
			        var totalMinutes = (durationElements[0] * 60) + durationElements[1];
			        graphData.push([startTimestamp, totalMinutes]);
			        jobRunTimes.push([data[i].identifier, new Date(startTimestamp).format('HH:MM dd mmm'), totalMinutes, totalMinutes != 1 ? 'mins' : 'min'].join(' '));
			    }
			    $.plot($("#history-graph"), [graphData], {
			            xaxis: { mode: 'time', show: false },
			            yaxis: { show: true, tickDecimals: 0 },
                        series: {
                            lines: { show: true },
                            points: { show: true }
                        },
                        grid: { hoverable: true, clickable: false }
			    });

			    function showTooltip(x, y, contents) {
                    $('<div id="tooltip">' + contents + '</div>').css( {
                        position: 'absolute',
                        display: 'none',
                        top: y + 5,
                        left: x + 5,
                        border: '1px solid #fdd',
                        padding: '2px',
                        'background-color': '#fee',
                        opacity: 0.80
                    }).appendTo("body").fadeIn(200);
                }

                var previousPoint = null;

                $("#history-graph").bind("plothover", function (event, pos, item) {
                    if (item) {
                        if (previousPoint != item.dataIndex) {
                            previousPoint = item.dataIndex;

                            $("#tooltip").remove();
                            var x = item.datapoint[0].toFixed(2),
                                y = item.datapoint[1].toFixed(2);

                            showTooltip(item.pageX, item.pageY,
                                        jobRunTimes[item.dataIndex]);
                        }
                    }
                    else {
                        $("#tooltip").remove();
                        previousPoint = null;
                    }
                });
			}
		});
}

function displayRunningJobs()
{
    invoke('build/runningJobs.json', function(data)
		{
			if(data)
			{
                var currentTime = new Date().getTime();
                var html = [];
                html.length = (7 * data.length) + 2;
                html.push('Running agents: ');
                html.push(data.length);
                html.push('<br/>');
                for(var i = 0, n = data.length; i < n; i++)
                {
                    html.push('<span>');
                    html.push(data[i].agentId);
                    html.push(' - ');
                    html.push(data[i].jobKey);
                    html.push(' (');
                    html.push(toTimeString(currentTime - Number(data[i].startedAt)));
                    html.push(')</span><br/>');
                }
                $('#running-jobs').html(html.join(''));
			}
		});
}

function clearRunningJobs()
{
    clearElement('#running-jobs');
}

function clearStats()
{
    clearElement('#build-details');
}

function clearElement(elementSelector)
{
    $(elementSelector).html('');
}

function isCurrentlyBuilding(buildStatus)
{
    return 'BUILDING' == buildStatus || 'WAITING_FOR_JOBS_TO_COMPLETE' == buildStatus;
}