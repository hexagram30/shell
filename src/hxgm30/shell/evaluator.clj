(ns hxgm30.shell.evaluator
  (:require
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.reader.grammar :as grammar])
  (:import
    (clojure.lang Keyword)))

(defn commands
  [^Keyword shell]
  (->> shell
       grammar/commands
       keys
       sort
       (map name)))

(defn help
  [^Keyword shell str-args]
  (let [[cmd & subcmds] (map keyword str-args)]
    (cond (nil? cmd)
          {:error :command-not-provided}

          (not (grammar/has-command? shell cmd))
          {:error :command-not-cound}

          (not (apply grammar/has-subcommand? [shell cmd subcmds]))
          {:error :subcommand-not-found}

          :else
          (let [help-text (apply grammar/subcommand-help
                                 [shell cmd subcmds])]
            (str formatter/new-line
                 (formatter/paragraph help-text))))))
