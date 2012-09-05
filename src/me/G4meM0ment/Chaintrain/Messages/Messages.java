package me.G4meM0ment.Chaintrain.Messages;

public enum Messages {
	
	newVersion				("New Version of Chaintrain is available: %v"),
	notEnoughMoney			("Not enough money to set a bounty."),
	minMoney				("You need to set %m minimal for an bounty."),
	newBounty				("A new bounty has been exposed."),
	bountyExposed			("Your bounty has been exposed."),
	bountyFinished			("You finished the bounty on %p, your reward: %m"),
	bountyAccepted			("Bounty accepted."),
	bountyCanceled			("Bounty canceled."),
	bountyAlreadyAccepted	("You already accepted this bounty."),
	bountyNotAccepted		("You dont have this bounty accepted."),
	playerNotChained		("Player not chained."),
	playerAlreadyChained	("Player already chained."),
	timeOverIn				("Minutes left: %t"),
	cantFindPlayer			("Cant find player."),
	chaining				("Chaining... %tr"),
	getChained				("You get chained... %tr"),
	moveWhileChained		("Unable to move."),
	dropWhileChained		("Unable to drop items."),
	attackWhileChained		("Unable to attack."),
	interactWhileChained	("Unable to interact."),
	shootWhileChained		("Unable to shoot while chained."),
	disabledCommand			("Cant use this command while chained."),
	allChained				("All players online now chained."),
	allUnchained			("All players online now unchained."),
	chained					("You chained %p."),
	chainedBy				("Chained by %p."),
	chainedOverTime			("Chained by %p over %t min."),
	chainedVictimOverTime	("Chained %p over %t min."),
	timeOver				("Time over - youre unchained."),
	unchainedChained		("%p have been unchained."),
	unchainedChainer		("Unchained by %p");
	
	private String msg;
	
	Messages(String msg) {
		this.msg = msg;
	}
	
	public String msg() {
		return msg;
	}
}
