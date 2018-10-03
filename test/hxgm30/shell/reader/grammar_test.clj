(ns hxgm30.shell.reader.grammar-test
  (:require
    [clojure.test :refer :all]
    [hxgm30.shell.formatter :as formatter]
    [hxgm30.shell.reader.grammar.core :as grammar]))

(def entry-grammar (grammar/create :entry))

(deftest commands
  (is (= [:commands :help :login :quit :register :reset]
         (sort (keys (grammar/commands entry-grammar)))))
  (is (= [:commands :help :login :quit :register :reset]
         (sort (grammar/commands entry-grammar {:as-keys true})))))

(deftest has-command?
  (is (grammar/has-command? entry-grammar :login))
  (is (not (grammar/has-command? entry-grammar :blurf)))
  (is (not (grammar/has-command? :quux :xyzzy))))

(deftest command
  (is (= {:help (str "Create a user account. Takes one argument, the user name. "
                     "The user will then be prompted to enter a password.")
          :fn #'hxgm30.registration.components.registrar/create-user}
         (grammar/command entry-grammar :register)))

  (is (= #'hxgm30.registration.components.registrar/create-user
         (grammar/command-fn entry-grammar :register))))

(deftest command-help
  (is (= (str "Perform one or more type of account resets.")
         (grammar/command-help entry-grammar :reset)))
  (is (= (str "Log into a game instance on the server. Providing a user "
              "name only will put you into the player shell. Providing a "
              "user name as well as a game instance will result in a list "
              "of character names that player has in the given game, one of "
              "which will need to be selected. Providing a user name, game "
              "instance, and a player (character) name will put you "
              "directly into the game world. In all cases, you will be "
              "prompted for a password."
              formatter/new-line
              "Supported game instances: :not-implemented")
         (grammar/command-help entry-grammar :login))))

(deftest login-fn
  (is (= :not-implemented
         ((grammar/command-fn entry-grammar :login)))))

(deftest has-subcommands?
  (is (not (grammar/has-subcommands? entry-grammar :login)))
  (is (not (grammar/has-subcommands? entry-grammar :register)))
  (is (grammar/has-subcommands? entry-grammar :reset)))

(deftest subcommands
  (is (= [:password :player-key]
         (sort (keys (grammar/subcommands entry-grammar :reset)))))
  (is (= [:password :player-key]
       (sort (grammar/subcommands
              entry-grammar :reset [] {:as-keys true}))))
  (is (= "password, player-key"
       (grammar/subcommands
        entry-grammar :reset [] {:comma-separated true}))))

(deftest subcommands-keys
  (is (= []
         (grammar/subcommands-keys nil)))
  (is (= []
         (grammar/subcommands-keys [])))
  (is (= [:subcommands :subcmd]
         (grammar/subcommands-keys ["subcmd"])))
  (is (= [:subcommands :subcmd1 :subcommands :subcmd2 :subcommands :subcmd3]
         (grammar/subcommands-keys ["subcmd1" "subcmd2" "subcmd3"]))))

(deftest get-in-command
  (is (= {:help "Reset the password for a given account."
          :fn #'hxgm30.registration.components.registrar/reset-password}
         (grammar/get-in-command entry-grammar [:reset "password"])))
  (is (not
        (nil? (grammar/get-in-command entry-grammar [:login]))))
  (is (nil? (grammar/get-in-command entry-grammar [:login "user"])))
  (is (nil? (grammar/get-in-command entry-grammar [:login "user" "world"]))))

(deftest subcommand
  (is (= {:help "Reset the password for a given account."
          :fn #'hxgm30.registration.components.registrar/reset-password}
         (grammar/subcommand entry-grammar :reset [:password])))
  (is (= "Reset the player key for a given account."
         (grammar/subcommand-help entry-grammar :reset [:player-key])))
  (is (= #'hxgm30.registration.components.registrar/reset-player-key
         (grammar/subcommand-fn entry-grammar :reset [:player-key]))))

(deftest callable?
  (is (grammar/callable? entry-grammar :login))
  (is (grammar/callable? entry-grammar :register))
  (is (grammar/callable? entry-grammar :reset [:password]))
  (is (grammar/callable? entry-grammar :reset [:player-key]))
  (is (not (grammar/callable? entry-grammar :help)))
  (is (not (grammar/callable? entry-grammar :reset))))
