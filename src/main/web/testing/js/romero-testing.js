var displayedTestRunHistoryItem;

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
            var html = [];
            html.push('<table class=\'test-results-table\'>')
            html.push('<tr><td colspan=\'3\' class=\'test-run-header\'>');
            html.push(formatJobRunIdentifier(jobId, timestamp));
            html.push('</td></tr>');
            for(var i = 0, n = responseData.length; i < n; i++)
            {
                var testSuiteResult = responseData[i];
                html.push('<tr><td colspan=\'3\' class=\'test-suite\'>');
                html.push(testSuiteResult.testClass);
                html.push('</td></tr>');

                var testCases = testSuiteResult.testExecutionResults;
                for(var j = 0, p = testCases.length; j < p; j++)
                {
                    var testCase = testCases[j];
                    var detail = ['<div class=\'test-case-detail\'>'];
                    var isFailure = testCase.testStatus == 'FAILURE' || testCase.testStatus == 'ERROR';
                    if(testCase.throwable)
                    {
                        detail.push('<div><span>Stack trace</span><pre>');
                        detail.push(testCase.throwable);
                        detail.push('</pre></div>');
                    }
                    detail.push('</div>');
                    

                    var detailsDivId = [testSuiteResult.testClass.replace(/\./g, '-'), '-', testCase.testMethod].join('');
                    html.push('<tr><td class=\'test-case\' onclick=\'toggleVisibility(\"');
                    html.push(detailsDivId);
                    html.push('\");\'><span class=\'');
                    if(isFailure)
                    {
                        html.push('failed-test-case');
                    }
                    html.push('\'>');
                    html.push(testCase.testMethod);
                    html.push('</span><div id=\'');
                    html.push(detailsDivId);
                    html.push('\' class=\'hidden\'>');
                    html.push(detail.join(''))
                    html.push('</div>');
                    html.push('</td><td class=\'test-status ');
                    html.push(testCase.testStatus);
                    html.push('\'>');
                    html.push(testCase.testStatus);
                    html.push('</td><td class=\'test-duration\'>');
                    html.push(toMinutesSecondsString(testCase.durationMillis));
                    html.push('</td></tr>');
                }
            }

            html.push('</table>')
            $('#test-results').html(html.join(''));
        }
    });
}

function formatJobRunIdentifier(jobRunId, timestamp)
{
    return [jobRunId, ' - ', formatBuildTimestamp(timestamp)].join('');
}

function getTestRunHistory()
{
    invoke('/testing/summary.json', function(data)
		{
			if(data)
			{
			    var firstEntry;
			    var html = ['<ul class=\'test-run-history-list\'>'];
			    for(var i = 0, n = data.length; i < n; i++)
			    {
			        var datum = data[i];
			        if(i == 0)
			        {
			            firstEntry = datum;
			        }
                    var jobId = datum.jobRunIdentifier;
                    var hasFailures = datum.statusCountMap.ERROR != 0 || datum.statusCountMap.FAILURE != 0 || datum.statusCountMap.TIMED_OUT != 0;

                    html.push('<li class=\'history-item\' onclick=\'getTestRunResults(\"');
                    html.push(datum.jobRunIdentifier);
                    html.push('\", ');
                    html.push(datum.startTimestamp);
                    html.push(');\'>')
                    html.push('<div class=\'');
                    html.push(hasFailures ? 'failed-icon' : 'passed-icon');
                    html.push('\'></div>');
                    html.push(formatJobRunIdentifier(jobId, datum.startTimestamp));
                    html.push(buildSummary(datum));
                    html.push('</li>');
			    }
			    html.push('</ul>')
                $('#test-run-history').html(html.join(''));
                if(!displayedTestRunHistoryItem)
                {
                    displayedTestRunHistoryItem = firstEntry;
                    getTestRunResults(displayedTestRunHistoryItem.jobRunIdentifier, displayedTestRunHistoryItem.startTimestamp);
                }
			}
		});
}

function toMinutesSecondsString(durationMillis)
{
    var timeElements = toTimeElements(durationMillis);
    return [(timeElements[1] + (timeElements[0] * 60)), 'm ', charPad(timeElements[2], '&nbsp;'), 's'].join('');
}