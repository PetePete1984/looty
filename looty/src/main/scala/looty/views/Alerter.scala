package looty
package views

import org.scalajs.jquery.JQueryStatic


//////////////////////////////////////////////////////////////
// Created by bjackman @ 1/1/14 1:58 PM
//////////////////////////////////////////////////////////////


object Alerter {

  val reloadMsgs = List(
    "This is going to take forever. Thanks, Obama!",
    "Alright you asked for it",
    "Take a nap this is going to take a while",
    "Stop moving your tabs!",
    "Reticulating splines",
    "You know you can press the buttons under refresh to refresh tabs invidually right? Oh you didn't? Well maybe next try that out."
  )

  val noReloadMsgs = List(
    "You chose wisely friend, had you said yes, I would've bitten your ear off.",
    "Please ask G.G.G. add Item Levels in the item descriptions, they say it confuses noobs, noobs, more like boobs hahahahahahahahahahahahahahahahahaha.",
    "Pan flute music is where it's at woooo hoooo doooo dooot poooot poooot",
    "Buy the new radioactive uranium microtransactions, make your character grow and glow",
    "Frank Stallone",
    "A dingo ate my baby kiwi",
    "You could say that Path of exile is the kiwi fruit of grinding gears games labors. you could say that. Then you go die.",
    "go go go go go go",
    "A wise decision, for as the correct choice holds life, the incorrect choice holds endless server reload time",
    "Kaom can cut through a hot knife with butter",
    "In Wraeclast ground burns you!",
    "Stay out of my fishing hole!"

  )

  val jq: JQueryStatic = global.jQuery.asInstanceOf[JQueryStatic]
  def info(msg: String) { display("info", msg) }
  def warn(msg: String) { display("warn", msg) }
  def error(msg: String) { display("error", msg) }
  private def display(cls: String, msg: String) {
    val el = jq("#alerter")
    el.empty()
    el.html(s"""<div class="$cls">$msg</div>""")
  }

  // Returns a random integer between min (included) and max (excluded)
  def getRandomInt(min : Int, max : Int) : Int = {
    (math.floor(math.random * (max - min)) + min).toInt
  }

  lazy val speaker = {
    global.meSpeak.loadConfig("jslib/mespeak/mespeak_config.json")
    global.meSpeak.loadVoice("jslib/mespeak/voices/en/en.json")
    global.meSpeak
  }

  def speak(msg : String) {
    speaker.speak(msg)
  }


  def infoSpeak(msg : String) {
    info(msg)
    speak(msg)
  }

  def reloadMsg() {
    infoSpeak(reloadMsgs(getRandomInt(0, reloadMsgs.size)))
  }

  def noReloadMsg() {
    infoSpeak(noReloadMsgs(getRandomInt(0, noReloadMsgs.size)))
  }

}