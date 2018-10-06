(ns hxgm30.shell.reader.grammar.common
  (:require
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.util :as util]))

(def help-usage
  "help <COMMAND> [<SUBCOMMAND> [<SUBCOMMAND> ...]]")

(def help
  {:help {
     :help (str "Get the documentation for supported commands and any of "
                "their subcommands. Usage is of the following form: "
                formatter/new-line
                help-usage)}})
