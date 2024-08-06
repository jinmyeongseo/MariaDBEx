function fadeIn(target) {
	let level = 0;
	let inTimer = null;
	inTimer = setInterval(function(){
		level = fadeInAction(target, level, inTimer);
	}, 50);
}

function fadeInAction(target, level, inTimer) {
	level = level + 0.1;
	if(level < 1) {
		target.style.opacity = level;
	} else {
		clearInterval(inTimer);
	}
	
	return level;
}

function fadeOut(target) {
	let level = 1;
	let inTimer = null;
	inTimer = setInterval(function(){
		level = fadeOutAction(target, level, inTimer);
	}, 50);
}

function fadeOutAction(target, level, inTimer) {
	level = level - 0.1;
	if(level > 0) {
		target.style.opacity = level;
	} else {
		clearInterval(inTimer);
	}
	
	return level;
}

function modalOpen(selector){
	const modal = document.querySelector(selector);
	
	modal.style.display = 'flex';
	fadeIn(modal);
}

function modalClose(selector){
	const modal = document.querySelector(selector);
	
	fadeOut(modal);
	modal.style.display = 'none';
}

window.addEventListener('load', (e) => {
	const popupELS = document.querySelectorAll('.popup-wrap');
	
	for(let popupEL of popupELS) {
		let closeELS = popupEL.querySelectorAll('.popup-close');
		
		for(let closeEL of closeELS) {
			closeEL.addEventListener('click', (e)=> {
				const modal = closeEL.closest('.popup-wrap');
				fadeOut(modal);
				modal.style.display = 'none';
				
				return;
			});
		}
	}
});