(ns hxgm30.shell.formatter
  (:require
    [clojure.string :as string]
    [hxgm30.common.util :as util]))

(def default-spaces-count 4)
(def default-list-indent-spaces 12)
(def default-wrap-width 76)
(def default-bullet-char "*")
(def new-line "\r\n") ; intended for use with Telnet

(def section-divider
  (str
    new-line
    new-line
    new-line
    "*   *   *   *   *"
    new-line
    new-line
    new-line))

(defn indent
  ([]
    (indent default-spaces-count))
  ([spaces-count]
    (repeat spaces-count \space)))

(defn list-item
  ([item]
    (list-item item default-list-indent-spaces))
  ([item list-indent-spaces]
    [new-line (indent list-indent-spaces) item]))

(defn list-items
  ([items]
    (list-items items {}))
  ([items passed-opts]
    (let [default-opts {:prefix-text ""
                        :prefix-indent-spaces default-spaces-count
                        :list-indent-spaces default-list-indent-spaces}
          opts (merge default-opts passed-opts)]
    (string/join
      ""
      (flatten [(indent (:prefix-indent-spaces opts))
                (:prefix-text opts)
                (map #(list-item % (:list-indent-spaces opts)) items)
                new-line])))))

(defn bullet-list-items
  ([list-items]
    (bullet-list-items list-items {}))
  ([list-items passed-opts]
    (let [default-opts {:prefix-text ""
                        :bullet-char default-bullet-char}
          opts (merge default-opts passed-opts)]
      (list-items
        (map #(format "%s %s%s" (:bullet-char opts) % new-line))
        opts))))

(defn paragraph
  ([long-line]
    (paragraph long-line {}))
  ([long-line passed-opts]
    (let [default-opts {:wrap-width default-wrap-width
                        :spaces-count default-spaces-count}
          opts (merge default-opts passed-opts)]
      (str
        (util/wrap-paragraph long-line
                             (:wrap-width opts)
                             (:spaces-count opts))
        new-line))))
