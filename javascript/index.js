var debug = false;
var visible = "maincontent";

function pageSwitch() {

	var mainContent = document.querySelector("body main .mainContent");
	var changelog = document.querySelector("body main .changelog");

	if (visible === "maincontent") {

		mainContent.style.left = "-100%";
		changelog.style.left = "0%";
		if (debug) console.log("Changelog is now visible.");
		visible = "changelog";

	} else if (visible === "changelog") {

		mainContent.style.left = "0%";
		changelog.style.left = "100%";
		if (debug) console.log("Main content is now visible.");
		visible = "maincontent";

	}

}

function goToPage(page) {

	var mainContent = document.querySelector("body main .mainContent");
	var changelog = document.querySelector("body main .changelog");

	if (page === "maincontent") {

		if (visible !== "maincontent") {

			mainContent.style.left = "0%";
			changelog.style.left = "100%";
			if (debug) console.log("Main content is now visible.");
			visible = "maincontent";

		} else {

			if (debug) console.log("Main content was already visible.");

		}

	} else if (page === "changelog") {

		if (visible !== "changelog") {

			mainContent.style.left = "-100%";
			changelog.style.left = "0%";
			if (debug) console.log("Changelog is now visible.");
			visible = "changelog";

		} else {

			if (debug) console.log("Changelog was already visible.");

		}

	}

}

window.onkeyup = function(e) {

	var key = e.keyCode ? e.keyCode : e.which;

	// 37 = left arrow
	// 39 = right arrow

	if (key == 37 && visible === "changelog") {

		goToPage("maincontent");

	} else if (key == 39 && visible === "maincontent") {

		goToPage("changelog");

	}

}