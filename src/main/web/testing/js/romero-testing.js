function buildSummary(datum)
{
    var html = [];
    html.push('<div id=\'');
    html.push(datum.startTimestamp);
    html.push('-');
    html.push(datum.jobRunIdentifier);
    html.push('\' class=\'test-run-summary hidden\'>Took ');
    html.push(toTimeString(datum.durationMillis));
    html.push('<br/>');
    html.push(datum.statusCountMap.ERROR);
    html.push(pluralise(' error', datum.statusCountMap.ERROR));
    html.push('<br/>');
    html.push(datum.statusCountMap.FAILURE);
    html.push(' failures<br/>Total test cases: ');
    html.push(datum.testCaseCount);
    html.push('</div>');
    return html.join('');
}

function getTestRunResults(jobId, timestamp)
{
    var postData = { jobRunIdentifier: jobId, startTimestamp: timestamp };
    invokeWithData('/testing/testResults.json', postData, function(responseData)
    {
        if(responseData)
        {
            
        }
    });
}

function getTestRunHistory()
{
    invoke('/testing/summary.json', function(data)
		{
			if(data)
			{
			    var html = ['<ul>'];
			    for(var i = 0, n = data.length; i < n; i++)
			    {

			        var datum = data[i];
                    var jobId = datum.jobRunIdentifier;
                    var jobDate = formatBuildTimestamp(datum.startTimestamp);
                    html.push('<li onclick=\'getTestRunResults(\"');
                    html.push(datum.jobRunIdentifier);
                    html.push('\", ');
                    html.push(datum.startTimestamp);
                    html.push(');\'>')
                    html.push(jobId);
                    html.push(' ');
                    html.push(jobDate);
                    html.push(buildSummary(datum));
                    html.push('</li>');
			    }
			    html.push('</ul>')
                $('#test-run-history').html(html.join(''));
			}
		});
}