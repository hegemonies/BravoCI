function add(form) {
    var elems = form.elements;
	var name = elems.Name.value;
	var repo = elems.Repository.value;

	$.ajax({
		url: "http://localhost:9090/add?name=" + name + "&repo=" + repo
	}).then(function(data) {
	    alert(data);
	});
}

function search(form) {
	const name = form.elements.Name.value;

	$.ajax({
		type: "POST",
		dataType: "json",
		url: "http://localhost:9090/search?name=" + name
	}).then(function (data) {
		var content = '<table>';
		content += '<tr>';
		content += '<td>' + 'Name' + '</td>';
		content += '<td>' + 'Repositories' + '</td>';
		content += '<td>' + 'Commits' + '</td>';
		content += '</tr>';

		for (var i = 0; i < data.repositories.length; i++) {
			content += '<tr>';

			for (var j = 0; j < data.repositories[i].commitInfos.length; j++) {
				if (i == 0 && j == 0) {
					content += '<td>' + data.name + '</td>';
				} else {
					content += '<td>' + '</td>';
				}

				if (j == 0) {
					content += '<td>' + data.repositories[i].name + '</td>';
				} else {
					content += '<td>' + '</td>';
				}

				var commitInfo = data.repositories[i].commitInfos[j].date + '<br>' +
						data.repositories[i].commitInfos[j].hashCommit + '<br>' +
						data.repositories[i].commitInfos[j].result + '<br>' +
						data.repositories[i].commitInfos[j].check;
				content += '<td>' + commitInfo + '</td>';

				content += '</tr>';
			}
		}

		content += '</table>';

		$('#user-history').html(content);
	}) 
}