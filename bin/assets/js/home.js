
console.log('TESTE');
$(document).ready(() => {
	const box = $('#box');
	const options = {
			url: 'https://gateway.marvel.com:443/v1/public/characters',
			type: 'GET',
			data: {
				apikey: 'd7df840624468d4936bab917b085b4ca',
				limit: 20
			},
			datatype: 'json'
	};

	$.ajax(options)
	.done(response => {
		if(response.code === 200){
			response.data.results.forEach(val => {
				
				const boxHero = `
				<div class="responsive">
					<div class="gallery">
						<a target="_blank" href="${val.thumbnail.path}.${val.thumbnail.extension}">
							<img src="${val.thumbnail.path}.${val.thumbnail.extension}" alt="${val.name}" width="600" height="400">
						</a>
						<div class="desc">${val.name}</div>
					</div>
				</div>`;

				box.append(boxHero);
			 });						
		} else {
			listBoxHeros.append('<h1>ERRO NA REQUISIÇÃO</h1>');
		}
	})
	.fail(() => {
		console.log('Erro');
		listBoxHeros.append('<h1>ERRO NA REQUISIÇÃO</h1>');
	});
});

