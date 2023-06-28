import {diff_match_patch} from "diff-match-patch";
import 'diff-match-patch-line-and-word';

function compareDiff(string1,string2) {
    const dmp = new diff_match_patch();
    const diffs = dmp.diff_wordMode(string1, string2);
    return diffs;
}
/*
function compareDiff(string1,string2) {
    let dmp = new diff_match_patch();
    let diff = dmp.diff_main(string1,string2)
    dmp.diff_cleanupSemantic(diff);
    // let diffHtml = dmp.diff_prettyHtml(diff);// trans to html
    return diff
}
*/
export {compareDiff}
