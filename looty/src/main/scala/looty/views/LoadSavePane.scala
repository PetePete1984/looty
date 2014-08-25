package looty
package views

import looty.views.loot.{Filters, Containers, Columns}
import org.scalajs.jquery.{JQueryEventObject, JQuery}

import scala.scalajs.js


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 8/24/14 6:35 PM
//////////////////////////////////////////////////////////////


class LoadSavePane(columns: Columns, containers: Containers, filters: Filters) {
  def start(): JQuery = {
    //Load / Save Stuff
    val loadSaveDiv = jq("""<div style="display:inline-block"></div>""")
    val loadDiv = jq("<div></div>")
    val loadBtn = jq("""<a href="javascript:void(0)" title="Loads the view with the name">[Load]</a>""")
    val saveBtn = jq("""<a href="javascript:void(0)" title="Saves the currently visible columns as a view">[Save]</a>""")
    val deleteBtn = jq("""<a href="javascript:void(0)" title="Deletes the current view">[Delete]</a>""")
    val saveWithFiltersBtn = jq("""<a href="javascript:void(0)" title="Saves the currently visible columns as well as any filters that are currently active">[Save+Filters]</a>""")
    loadSaveDiv.append("""<span title="Views are customizable groups of ">Views:</span>""")
    loadSaveDiv.append(loadDiv)
    loadSaveDiv.append(loadBtn)
    loadSaveDiv.append(saveBtn)
    loadSaveDiv.append(saveWithFiltersBtn)
    loadSaveDiv.append(deleteBtn)



    val O = js.Dynamic.literal

    val loadSel = loadDiv.asJsDyn.select2(O(
      width = 120,
      placeholder = "Name",
      query = { (q: js.Dynamic) =>
        val names = Saver.getAllNames
        val term = q.term.asInstanceOf[String]
        val create = if (term.nonEmpty && names.forall(_.toLowerCase != term.toLowerCase)) {
          List(O(id = term, text = s"New: $term"))
        } else {
          Nil
        }
        val vs = (create ++ names.filter(_.toLowerCase.startsWith(term.toLowerCase)).map(n => O(id = n, text = n))).toJsArray
        q.callback(O(results = vs))
      }: js.Function
    ))

    loadBtn.on("click", () => {
      val name = loadSel.`val`().asInstanceOf[String]
      if (name != null && name.nonEmpty) {
        Saver.load(name)(colId => columns.get(colId)) foreach {
          case (cols, colFilters, conIds) =>
            columns.all.foreach(_.hide())
            cols.foreach(_.show())
            colFilters.foreach { colFilters =>
              filters.clearColumnFilters()
              colFilters.foreach { colFilter =>
                filters.addColFilter(colFilter)
              }
              filters.refresh()
            }
            conIds.foreach { conIds =>
              containers.all.foreach(_.hide())
              conIds.foreach { conId =>
                containers.get(conId).foreach(_.show())
              }
              filters.refresh()
            }
            Alerter.info(s"Loaded view: $name")
        }
        false
      }
    })

    saveBtn.on("click", () => {
      val name = loadSel.`val`().asInstanceOf[String]
      if (name != null && name.nonEmpty) {
        Saver.save(name, columns.visible, None, None)
        Alerter.info(s"Saved view: $name")
      }
      false
    })

    saveWithFiltersBtn.on("click", () => {
      val name = loadSel.`val`().asInstanceOf[String]
      if (name != null && name.nonEmpty) {
        Saver.save(
          name,
          columns.visible,
          columnFilters = Some(filters.columnFilters.values.toVector),
          containerFilters = Some(filters.containerFilters.toVector)
        )
        Alerter.info(s"Saved view: $name")
      }
      false
    })

    deleteBtn.on("click", () => {
      val name = loadSel.`val`().asInstanceOf[String]
      if (name != null && name.nonEmpty) {
        Saver.delete(name)
        Alerter.info(s"Deleted view: $name")
      }
      false
    })

    loadSaveDiv
  }
}