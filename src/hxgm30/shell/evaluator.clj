(ns hxgm30.shell.evaluator
  (:require
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.reader.grammar :as grammar])
  (:import
    (clojure.lang Keyword)))

(defn commands
  [^Keyword shell]
  (str formatter/new-line
       (grammar/commands shell
                         {:as-item-list true
                          :prefix-text (str "Available commands:"
                                            formatter/new-line)})
       formatter/new-line
       (formatter/paragraph (str "To view the help for a command, "
                                 "type 'help <COMMAND>'."))))

(defn help
  [^Keyword shell str-args]
  (let [[cmd & subcmds] (map keyword str-args)]
    (cond (nil? cmd)
          (commands shell)

          (not (grammar/has-command? shell cmd))
          {:error :command-not-cound}

          (not (apply grammar/has-subcommands? shell cmd subcmds))
          {:error :subcommand-not-found}

          :else
          (let [help-text (grammar/subcommand-help shell cmd subcmds)]
            (str formatter/new-line
                 (formatter/paragraph help-text)
                 (when (grammar/has-subcommands? shell cmd subcmds)
                  (grammar/subcommands
                   shell
                   cmd
                   subcmds
                   {:as-item-list true
                    :prefix-text (str "Available subcommands:"
                                      formatter/new-line)}))
                 formatter/new-line
                 (formatter/paragraph
                  (str "To view the help for a subcommand, type "
                       "'help <COMMAND> <SUBCOMMAND>'")))))))
