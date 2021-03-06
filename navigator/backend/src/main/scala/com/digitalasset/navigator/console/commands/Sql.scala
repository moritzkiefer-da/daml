// Copyright (c) 2019 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.digitalasset.navigator.console.commands

import com.digitalasset.navigator.console._

case object Sql extends SimpleCommand {
  def name: String = "sql"

  def description: String = "Execute a SQL query"

  def params: List[Parameter] = List(
    ParameterSQL("query", "The SQL query")
  )

  def eval(
      state: State,
      args: List[String],
      set: CommandSet): Either[CommandError, (State, String)] = {
    val query = args.mkString(" ")
    for {
      ps <- state.getPartyState ~> s"Unknown party ${state.party}"
      result <- ps.ledger.runQuery(query) ~> s"Error while running the query"
    } yield {
      val table = AsciiTable()
        .width(state.reader.getTerminal.getWidth)
        .multiline(true)
        .columnMinWidth(4)
        .sampleAtMostRows(100000)
        .rowMaxHeight(500)
        .header(result.columnNames)
        .rows(result.rows)
        .toString
      (state, table)
    }
  }

}
