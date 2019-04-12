function add(form) {
    var elems = form.elements;
	var name = elems.Name.value;
	var repo = elems.Repository.value;

	$.ajax({
		url: "http://localhost:8080/add?name=" + name + "&repo=" + repo
	}).then(function(data) {
	    alert(data);
	});
}

function search(form) {
	const name = form.elements.Name.value;

	$.ajax({
		type: "POST",
		dataType: "text",
		url: "http://localhost:8080/search?name=" + name
	}).then(function (data) {
	    console.log(data);

		var content = '<table>';
		content += '<tr>';
		content += '<td>' + 'Name' + '</td>';
		content += '<td>' + 'Repositories' + '</td>';
		content += '<td>' + 'Commits' + '</td>';
		content += '</tr>';

		var json = JSON.parse(data);

		for (var i = 0; i < json.length; i++) {
			content += '<tr>';

			content += '<td>' + json[i]['name'] + '</td>';

			for (var j = 0; j < json[i].repositories.length; j++) {
				content += '<td>' + json[i].repositories[j]['name'] + '</td>';

				for (var k = 0; k < json[i].repositories[j].length; k++) {
					content += '<td>' + json[i].repositories[j]['commitInfos'][k] + '</td>';
					content += '</tr>';
				}
			}
		}

		content += '</table>';

		$('#user-history').html(content);
	}) 
}