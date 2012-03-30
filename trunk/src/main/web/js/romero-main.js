function getBuildStatus()
{
    invoke('build/status.json', function(data)
		{
			if(data)
			{
                var buildStatus = data.status;
				$('#build-status').text(data.status)
                $('#build-details').html(['<span>Total jobs: ', data.totalJobs, '</span><br/><span>Remaining jobs:', data.remainingJobs, '</span>'].join(''));
                if(isCurrentlyBuilding(buildStatus))
                {
                    displayRunningJobs();
                }
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
                html.push('Running agents<br/>');
                for(var i = 0, n = data.length; i < n; i++)
                {
                    html.push('<span>');
                    html.push(data[i].agentId);
                    html.push(' - ');
                    html.push(data[i].jobKey.testClass);
                    html.push(' (');
                    html.push(toTimeString(currentTime - Number(data[i].startedTimestamp)));
                    html.push(')</span><br/>');
                }
                $('#running-jobs').html(html.join(''));
			}
		});
}


function isCurrentlyBuilding(buildStatus)
{
    return 'BUILDING' == buildStatus || 'WAITING_FOR_TESTS_TO_COMPLETE' == buildStatus;
}