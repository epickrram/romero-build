function pluralise(token, count)
{
    if(count !== 1)
    {
        return token + 's';
    }
    return token;
}

function toTimeString(durationMillis)
{
    var timeElements = toTimeElements(durationMillis);
    return [zeroPad(timeElements[0]), zeroPad(timeElements[1]), zeroPad(timeElements[2])].join(':');
}

function toTimeElements(durationMillis)
{
    var millis = durationMillis % 1000;
    var totalSeconds = Math.floor(durationMillis / 1000);
    var seconds = totalSeconds % 60;
    var minutes = Math.floor((totalSeconds - seconds) / 60) % 60;
    var hours = Math.floor((totalSeconds - seconds - (60 * minutes))/3600);

    return [hours, minutes, seconds];
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

function formatBuildTimestamp(timestamp)
{
    return new Date(timestamp).format('HH:MM dd mmm');
}
