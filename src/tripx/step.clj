(ns tripx.step)

(defmulti take-step (fn [state {:keys [step]}] step))
