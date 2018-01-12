import * as types from "../actions/types";

const initialState = {
  showPanel: false,
  activeId: "0"
};

export default function(state = initialState, action) {
  switch (action.type) {
    case types.TOGGLE_PANEL:
      return {
        ...state,
        showPanel: action.showPanel
      };
      break;

    default:
      return state;
      break;
  }
}
