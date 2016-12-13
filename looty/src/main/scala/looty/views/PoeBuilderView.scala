package looty
package views

import looty.model.PassiveSkillTreeHelp
import looty.poeapi.PoeTypes.CharacterInfo
import looty.poeapi.{PoeRpcs, PoeCacher}
import org.scalajs.jquery.{JQuery, JQueryStatic}

import scala.scalajs.js
import scala.util.Failure
import scala.util.Success


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/22/14 5:27 PM
//////////////////////////////////////////////////////////////

class PoeBuilderView(implicit val pc: PoeCacher) extends View {
  val jq: JQueryStatic = global.jQuery.asInstanceOf[JQueryStatic]
  override def start(el: JQuery): Unit = {
    var charMap = Map.empty[String, CharacterInfo]
    val playerDiv = jq("<div></div>")
    //IFRAME DISABLED UNTIL POE BUILDER STOPS BLOCKING IFRAMES
    //val iframe = jq("""<iframe class="poebuilder"></iframe>""")
    //Add a list of buttons one per character
    val O = js.Dynamic.literal
    el.append(playerDiv)
    playerDiv.asJsDyn.select2(O(
      width = 180,
      placeholder = "Character",
      query = { (q: js.Dynamic) =>
        val term = q.term.asInstanceOf[String]
        for {
          chars <- pc.getChars()
        } {
          charMap = Map(chars.toList.map(c => c.name -> c): _*)
          val cs = chars.toList
            .filter(c => c.name.toLowerCase.startsWith(term.toLowerCase))
            .sortBy(_.name.toLowerCase)
            .map(c => O(id = c.name, text = c.name))
            .toJsArr
          q.callback(O(results = cs))
        }
      }: js.Function
    )).on("change", { (e: js.Dynamic) =>
      Alerter.info("Attempting to get passive skill tree try from path of exile.com...")
      val charName = e.`val`.asInstanceOf[String]
      pc.getAccountName.onComplete {
        case Success(accountName) =>
          PoeRpcs.getPassiveSkills(accountName = accountName, character = charName).foreach { data =>
            val base64 = PassiveSkillTreeHelp.decode(charMap(charName).getCharClass, data.hashes.asJsArr[Int])
            //iframe.attr("src", s"http://poebuilder.com/character/$base64")
            //window.open(s"http://poebuilder.com/character/$base64")
            window.open(s"https://www.pathofexile.com/passive-skill-tree/$base64")
          }
        case Failure(ex) =>
          Alerter.error(s"""No account name specified ($ex) please set it manually here: <a href="#/settings">here</a>""")
      }
    }: js.Function)

    //    el.append(playerSel)
    //el.append(iframe)
  }
  override def stop(): Unit = {

  }
}