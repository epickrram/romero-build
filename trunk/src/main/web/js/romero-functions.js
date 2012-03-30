function toTimeString(durationMillis)
{
    var millis = durationMillis % 1000;
    var totalSeconds = Math.floor(durationMillis / 1000);
    var seconds = totalSeconds % 60;
    var minutes = Math.floor((totalSeconds - seconds) / 60) % 60;
    var hours = Math.floor((totalSeconds - seconds - (60 * minutes))/3600);

    return [zeroPad(hours), zeroPad(minutes), zeroPad(seconds)].join(':');
}

function zeroPad(value)
{
    return value < 10 ? '0'+value : ''+value;
}

function invoke(postUrl, successHandler)
{
	$.ajax({
		url: postUrl,
        type: 'POST',
		success: successHandler
	});
}
