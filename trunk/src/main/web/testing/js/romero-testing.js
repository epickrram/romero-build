/*
durationMillis: 60055
jobRunIdentifier: "125"
startTimestamp: 1335335275572
statusCountMap: Object
ERROR: 1
FAILURE: 1
IGNORED: 1
SUCCESS: 4
__proto__: Object
testCaseCount: 7
testSuiteCount: 3
*/

function buildSummary(datum)
{
    var html = [];
    html.push('<div id=\'');
    html.push(datum.startTimestamp);
    html.push('-');
    html.push(datum.jobRunIdentifier);
    html.push('\' style=\'test-run-summary\'>Took ');
    html.push(toTimeString(datum.durationMillis));
    html.push('<br/>');
    html.push(datum.statusCountMap.ERROR);
    html.push(pluralise(' error', datum.statusCountMap.ERROR));
    html.push('<br/>');
    html.push(datum.statusCountMap.FAILURE);
    html.push(' failures<br/>Total test cases: ');
    html.push(datum.testCaseCount);
    html.push();
    html.push('</div>');
    return html.join('');
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
                    html.push('<li>');
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