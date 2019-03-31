function add(form) {
    var elems = form.elements;
	var name = elems.Name.value;
	var repo = elems.Repository.value;

	$.ajax({
		url: "http://localhost:8080/add?name=" + name + "&repo=" + repo
	}).then(function (data) {
        alert(data);
    });
}

function search(form) {
	const name = form.elements.Name.value;

	const text = "{'_id':'5ca03cb8160bc126cf7e023e','name':'patrick','repositories':[{'name':'helloworld','commitInfos':['afnaom','3f3fm3','01fab']},{'name':'RenderEngine','commitInfos':['groga33','342lmb','09ba0j']}],'_class':'BravoCI.Frontend.User'}";

	$.ajax({
		type: "POST",
		dataType: "text",
		url: "http://localhost:8080/search?name=" + name
	}).then(function (data) {
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

			for (var j = 0; j < json[i].repositories.lenght; j++) {
				content += '<td>' + json[i].repositories[j]['name'] + '</td>';

				for (var k = 0; k < json[i].repositories[j].lenght; k++) {
					content += '<td>' + json[i].repositories[j]['commitInfos'][k] + '</td>';
					content += '</tr>';
				}
			}
		}

		content += '</table>';

		$('#user-history').html(content);
	}) 
}