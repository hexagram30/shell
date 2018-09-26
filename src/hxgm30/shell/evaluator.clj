(ns hxgm30.shell.evaluator
  (:require
    [clojure.string :as string]
    [hxgm30.shell.errors :as errors]
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.reader.grammar.core :as grammar]
    [taoensso.timbre :as log]))

(def help-all
  "To see a list of available commands, type 'commands' or 'help'.")

(def help-cmd
  "To view the help for a command, type 'help <COMMAND>'.")

(def help-subcmd
  "To view the help for a subcommand, type 'help <COMMAND> <SUBCOMMAND>'.")

(defn no-command
  [gmr cmd]
  (let [cmd-keys (grammar/fuzzy-command-keys gmr cmd)]
    (str formatter/new-line
         (formatter/paragraph
          (if-not (empty? cmd-keys)
             (format errors/typoed-command
                     (name cmd)
                     (string/join "', '" (map name cmd-keys)))
             (format errors/cmd-unknown (name cmd))))
         (formatter/paragraph (str help-all " " help-cmd " " help-subcmd)))))

(defn commands
  [gmr]
  (str formatter/new-line
       (grammar/commands gmr
                         {:as-item-list true
                          :prefix-text (str "Available commands:"
                                            formatter/new-line)})
       (formatter/paragraph help-cmd)))

(defn- -subcmd-help-text
  [gmr cmd subcmds]
  [(grammar/subcommands
    gmr
    cmd
    subcmds
    {:as-item-list true
     :prefix-text (str "Available subcommands:" formatter/new-line)})
   formatter/new-line
   (formatter/paragraph help-subcmd)])

(defn- -cmd-help-text
  [gmr cmd subcmds]
  (log/trace "grammar:" gmr)
  (log/trace "cmd:" cmd)
  (log/trace "subcmds:" subcmds)
  (log/trace "(cons cmd subcmds):" (cons cmd subcmds))
  (log/tracef "(grammar/has-keys? gmr %s): %s"
              (cons cmd subcmds)
              (grammar/has-keys? gmr (cons cmd subcmds)))
  (log/tracef "(grammar/has-subcommands? gmr %s %s): %s"
              cmd
              subcmds
              (grammar/has-subcommands? gmr cmd subcmds))
  (concat [formatter/new-line
           (formatter/paragraph (grammar/subcommand-help gmr cmd subcmds))]
          (when (grammar/has-subcommands? gmr cmd subcmds)
            (-subcmd-help-text gmr cmd subcmds))))

(defn help
  [gmr str-args]
  (log/trace "grammar:" gmr)
  (log/trace "str-args:" str-args)
  (let [[cmd & _subcmds] (mapv keyword str-args)
        subcmds (vec _subcmds)]
    (log/tracef "cmd: %s (type: %s)" cmd (type cmd))
    (log/tracef "subcmds: %s (type: %s)" subcmds (type subcmds))
    (cond (nil? cmd)
          (commands gmr)

          (not (grammar/has-command? gmr cmd))
          {:error :command-not-found}

          (and (not (or (nil? subcmds)
                        (empty? subcmds)))
               (not (apply grammar/has-keys? [gmr (cons cmd subcmds)])))
          {:error :subcommand-not-found}

          :else
          (string/join "" (-cmd-help-text gmr cmd subcmds)))))
