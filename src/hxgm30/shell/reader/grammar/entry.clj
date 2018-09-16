(in-ns 'hxgm30.shell.reader.grammar)

(def entry-command-tree
  {:entry {
     :help "Top-level commands available upon connection to the server."
     :commands {
       :login {
         :help "Log in to the server."
       }
       :register {
         :help "Create a user account"
         :fn #'hxgm30.registration.components.registrar/create-user}
       :reset {
         :help "Perform one or more type of account results."
         :subcommands {
           :password  {
             :help "Reset the password for a given account."
             :fn #'hxgm30.registration.components.registrar/reset-password}
           :player-key {
             :help "Reset the player key for a given account."
             :fn #'hxgm30.registration.components.registrar/reset-player-key}}}}}})
