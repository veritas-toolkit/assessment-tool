import {diff_match_patch} from "diff-match-patch";

function compareDiff(string1,string2) {
    let dmp = new diff_match_patch();
    let diff = dmp.diff_main(string1,string2)
    dmp.diff_cleanupSemantic(diff);
    return diff
}
export {compareDiff}
