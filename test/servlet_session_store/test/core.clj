(ns servlet-session-store.test.core
  (:use [servlet-session-store.core]
        [ring.middleware.session.store])
  (:use [clojure.test])
  (:import (javax.servlet.http HttpServletRequest HttpSession)))

(deftest create-servlet-session-store-failures
  (is (thrown? IllegalArgumentException (servlet-session-store nil)))
  (is (thrown? IllegalArgumentException (servlet-session-store {}))))

(def hs
  (let [state (atom {})]
    (reify HttpSession
      (getAttribute [_ param] (@state param))
      (setAttribute [_ param value] (swap! state assoc param value))
      (removeAttribute [_ param] (swap! state dissoc param)))))

(def request {:servlet-request (reify HttpServletRequest
                                 (getSession [_] hs))})

(deftest create-servlet-session-store
  (is (satisfies? SessionStore (servlet-session-store hs)))
  (is (satisfies? SessionStore (servlet-session-store request))))

(deftest exercise-servlet-session-store
  (let [s (servlet-session-store hs)]
    (is (nil? (read-session s "foo")))
    (do
      (write-session s "foo" "bar")
      (is (= "bar" (read-session s "foo")))
      (delete-session s "foo")
      (is (nil? (read-session s "foo"))))))

(deftest servlet-session-middleware
  ((wrap-servlet-session
    (fn [req]
      (is
       (satisfies? SessionStore (:session req)))))
   request))
