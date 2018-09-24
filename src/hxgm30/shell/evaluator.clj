(ns hxgm30.shell.evaluator
  (:require
    [clojure.string :as string]
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.reader.grammar :as grammar]
    [taoensso.timbre :as log])
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

(defn- -subcmd-help-text
  [shell cmd subcmds]
  [(grammar/subcommands
    shell
    cmd
    subcmds
    {:as-item-list true
     :prefix-text (str "Available subcommands:"
                       formatter/new-line)})
   formatter/new-line
   (formatter/paragraph
     (str "To view the help for a subcommand, type "
         "'help <COMMAND> <SUBCOMMAND>'"))])

(defn- -cmd-help-text
  [shell cmd subcmds]
  (log/warn "shell:" shell)
  (log/warn "cmd:" cmd)
  (log/warn "subcmds:" subcmds)
  (concat [formatter/new-line
           (formatter/paragraph (grammar/subcommand-help shell cmd subcmds))]
          (when (grammar/has-keys? shell (cons cmd subcmds))
            (-subcmd-help-text shell cmd subcmds))))

(defn help
  [^Keyword shell str-args]
  (log/debug "shell:" shell)
  (log/debug "str-args:" str-args)
  (let [[cmd & _subcmds] (mapv keyword str-args)
        subcmds (vec _subcmds)]
    (log/debugf "cmd: %s (type: %s)" cmd (type cmd))
    (log/debugf "subcmds: %s (type: %s)" subcmds (type subcmds))
    (cond (nil? cmd)
          (commands shell)

          (not (grammar/has-command? shell cmd))
          {:error :command-not-cound}

          (and (not (or (nil? subcmds)
                        (empty? subcmds)))
               (not (apply grammar/has-keys? [shell (cons cmd subcmds)])))
          {:error :subcommand-not-found}

          :else
          (string/join "" (-cmd-help-text shell cmd subcmds)))))
