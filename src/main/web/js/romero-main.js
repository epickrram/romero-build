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
			    var html = [];
			    for(var i = 0, n = data.length; i < n; i++)
			    {
			        html.push('<div class=\'history-entry\'>');
			        html.push(data[i].identifier);
			        html.push(': <span class=\'history-entry-date\'>');
			        html.push(new Date(data[i].startTimestamp).format('HH:MM dd mmm'))
			        html.push(' (');
			        var durationElements = toTimeElements(data[i].endTimestamp - data[i].startTimestamp);
			        var totalMinutes = (durationElements[0] * 60) + durationElements[1];
			        html.push(totalMinutes)
			        html.push(totalMinutes > 1 ? ' mins' : ' min');
			        html.push(')</span></div>')
			    }
                $('#history').html(html.join(''));
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