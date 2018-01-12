import * as types from "./types";

export default function togglePanel(showPanel) {
  return {
    type: types.TOGGLE_PANEL,
    showPanel
  };
}
