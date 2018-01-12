import { combineReducers } from "redux";
import panelReducer from "./panel";

const rootReducers = combineReducers({
  panel: panelReducer
});

export default rootReducers;
