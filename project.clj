(defproject library-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [environ             "1.1.0"]
                 [morse               "0.2.4"]
                 [compojure "1.7.0"]
                 [clj-http "3.12.3"]
                 [cheshire "5.11.0"]
                 [etaoin "0.4.6"]
                 [babashka "0.9.161"]
                 [babashka/fs "0.1.6"]
                 ;; [clj-tagsoup "0.3.0"]
                 ;; [clj-tagsoup/clj-tagsoup "0.3.0"]
                 [hickory "0.7.1"]]

  :plugins [[lein-environ "1.1.0"]]

  :main ^:skip-aot library-bot.core
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
