(in-ns 'hxgm30.shell.reader.grammar)

(def entry-command-tree
  {:entry {
     :help "Top-level commands available upon connection to the server."
     :commands {
       :help {
         :help (str "Get the documentation for supported commands and any of "
                    "their subcommands. Usage is of the following form: "
                    "help <COMMAND> [<SUBCOMMAND> [<SUBCOMMAND> ...]]")}
       :login {
         :help (str "Log in to the server. Takes one argument, the user name. "
                    "The user will then be prompted to enter their password.")
         :fn (constantly :undefined) ; This needs to be provided by the master
                                     ; project/game server.
         }
       :register {
         :help (str "Create a user account. Takes one argument, the user name. "
                    "The user will then be prompted to enter a password.")
         :fn #'hxgm30.registration.components.registrar/create-user}
       :reset {
         :help "Perform one or more type of account resets."
         :subcommands {
           :password  {
             :help "Reset the password for a given account."
             :fn #'hxgm30.registration.components.registrar/reset-password}
           :player-key {
             :help "Reset the player key for a given account."
             :fn #'hxgm30.registration.components.registrar/reset-player-key}}}}}})
