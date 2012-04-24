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
                    html.push('</li>');
			    }
			    html.push('</ul>')
                $('#test-run-history').html(html.join(''));
			}
		});
}