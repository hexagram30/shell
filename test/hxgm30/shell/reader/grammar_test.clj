(ns hxgm30.shell.reader.grammar-test
  (:require
    [clojure.test :refer :all]
    [hxgm30.shell.reader.grammar :as grammar]))

(deftest has-shell?
  (is (grammar/has-shell? :entry))
  (is (not (grammar/has-shell? :quux))))

(deftest commands
  (is (= [:commands :help :login :quit :register :reset]
         (sort (keys (grammar/commands :entry)))))
  (is (= [:commands :help :login :quit :register :reset]
         (sort (grammar/commands :entry {:as-keys true})))))

(deftest has-command?
  (is (grammar/has-command? :entry :login))
  (is (not (grammar/has-command? :entry :blurf)))
  (is (not (grammar/has-command? :quux :xyzzy))))

(deftest command
  (is (= {:help (str "Create a user account. Takes one argument, the user name. "
                     "The user will then be prompted to enter a password.")
          :fn #'hxgm30.registration.components.registrar/create-user}
         (grammar/command :entry :register)))

  (is (= #'hxgm30.registration.components.registrar/create-user
         (grammar/command-fn :entry :register))))

(deftest command-help
  (is (= (str "Perform one or more type of account resets.\n\nSupported "
              "subcommands: password, player-key")
         (grammar/command-help :entry :reset)))
  (is (= (str "Log in to a game instance on the server. Takes two arguments: "
              "the user name and the game instance to join. The user will "
              "then be prompted to enter their password.\n\nSupported game "
              "instances: :not-implemented")
         (grammar/command-help :entry :login))))

(deftest login-fn
  (is (= :not-implemented
         ((grammar/command-fn :entry :login)))))

(deftest has-subcommands?
  (is (not (grammar/has-subcommands? :entry :login)))
  (is (not (grammar/has-subcommands? :entry :register)))
  (is (grammar/has-subcommands? :entry :reset)))

(deftest subcommands
  (is (= [:password :player-key]
         (sort (keys (grammar/subcommands :entry :reset)))))
  (is (= [:password :player-key]
       (sort (grammar/subcommands :entry :reset {:as-keys true}))))
  (is (= "password, player-key"
       (grammar/subcommands :entry :reset {:comma-separated true}))))

(deftest subcommands-keys
  (is (= []
         (grammar/subcommands-keys nil)))
  (is (= []
         (grammar/subcommands-keys [])))
  (is (= [:subcommands :subcmd]
         (grammar/subcommands-keys ["subcmd"])))
  (is (= [:subcommands :subcmd1 :subcommands :subcmd2 :subcommands :subcmd3]
         (grammar/subcommands-keys ["subcmd1" "subcmd2" "subcmd3"]))))

(deftest keys->subcommand
  (is (= {:help "Reset the password for a given account."
          :fn #'hxgm30.registration.components.registrar/reset-password}
         (grammar/keys->subcommand :entry :reset ["password"])))
  (is (not
        (nil? (grammar/keys->subcommand :entry :login []))))
  (is (nil? (grammar/keys->subcommand :entry :login ["user"])))
  (is (nil? (grammar/keys->subcommand :entry :login ["user" "world"]))))

(deftest subcommand
  (is (= {:help "Reset the password for a given account."
          :fn #'hxgm30.registration.components.registrar/reset-password}
         (grammar/subcommand :entry :reset [:password])))
  (is (= "Reset the player key for a given account."
         (grammar/subcommand-help :entry :reset [:player-key])))
  (is (= #'hxgm30.registration.components.registrar/reset-player-key
         (grammar/subcommand-fn :entry :reset [:player-key]))))

(deftest callable?
  (is (grammar/callable? :entry :login))
  (is (grammar/callable? :entry :register))
  (is (grammar/callable? :entry :reset [:password]))
  (is (grammar/callable? :entry :reset [:player-key]))
  (is (not (grammar/callable? :entry :help)))
  (is (not (grammar/callable? :entry :reset))))
