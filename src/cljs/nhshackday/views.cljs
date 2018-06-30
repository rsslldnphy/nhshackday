(ns nhshackday.views
  (:require
    [re-frame.core :as re-frame]
    [reagent.core :as reagent]
    [nhshackday.subs :as subs]))

(def blue "#B0C4DE")
(def grey "#efefef")

(def steps
  [{:id      :first-question
    :text    "Are you suffering from chest pain?"
    :options [{:id :yes :text "Yes" :next-step :a_and_e}
              {:id :no  :text "No"  :next-step :fever}]}
   {:id   :a_and_e
    :text "Go to A&E"}])

(def steps-lookup
  (into {} (map (juxt :id identity) steps)))

(def initial-state
  {:current-step    (:first-question steps-lookup)
   :completed-steps []})

(defn choose-answer
  [state step option]
  (-> state
      (update :completed-steps conj (assoc step :answer option))
      (assoc :current-step (get steps-lookup (:next-step option)))))

(defn main-panel []
  (let [state (reagent/atom initial-state)]
    (fn []
      [:div [:pre (pr-str @state)]
       (let [{:keys [current-step completed-steps]} @state]
         [:div
         (into 
           (for [step completed-steps]
             ^{:key (:id step)}
             [:div
              [:h2 {:style { :margin-bottom "25px"}} (:text step)]
              [:ul.list-inline
               (for [option (:options step)]
                 ^{:key (:id option)}
                 [:li {:style {:width "50%" :margin "0" :padding-right "0"}}
                  [:label {:name (:id option)
                           :style { :padding "15px" :background (if (= option (:answer step)) blue grey) :text-align "center" :margin-bottom "5px" :width "100%"}}
                   (str " " (:text option))]])]]))
         [:div
          [:h2 {:style { :margin-bottom "25px"}} (:text current-step)]
          (when-let [options (:options current-step)]
            [:ul.list-inline
             (for [option options]
               ^{:key (:id option)}
               [:li {:on-click #(do (.preventDefault %)
                                    (swap! state choose-answer current-step option))
                     :style {:width "50%" :margin "0" :padding-right "0"}}
                [:label {:name (:id option)
                         :style { :padding "15px" :background grey :text-align "center" :margin-bottom "5px" :width "100%" :cursor "pointer"}}
                 [:input {:id (:id option)
                          :type "checkbox"
                          :style {:visibility "hidden"}}] (str " " (:text option))]])])]])])))
