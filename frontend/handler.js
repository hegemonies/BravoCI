function add(form) {
    var elems = form.elements;
	var name = elems.Name.value;
	var repo = elems.Repository.value;

	$.ajax({
		url: "http://localhost:8080/add?Name=" + name + "&repo=" + phone
	}).then(function (data) {
        alert(data);
    });
}
