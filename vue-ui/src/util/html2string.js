import el from "element-ui/src/locale/lang/el";

function html2string(data) {
    let res = {}
    // let string = '<p style=\\"padding-left: 40px;\\">1312314<strong>43432</strong>&lt;&amp;lt;p&gt;</p>\\n<table style=\\"border-collapse: collapse; width: 100.068%; height: 65.25px;\\" border=\\"1\\">\\n<tbody>\\n<tr style=\\"height: 21.75px;\\">\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">而而为</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n</tr>\\n<tr style=\\"height: 21.75px;\\">\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n</tr>\\n<tr style=\\"height: 21.75px;\\">\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 19.9789%; height: 21.75px;\\">&nbsp;</td>\\n</tr>\\n</tbody>\\n</table>\\n<p>&nbsp;</p>\\n<table style=\\"border-collapse: collapse; width: 99.9989%; height: 43.5px;\\" border=\\"1\\">\\n<tbody>\\n<tr style=\\"height: 21.75px;\\">\\n<td style=\\"width: 33.1063%; height: 21.75px;\\">而erect而</td>\\n<td style=\\"width: 33.1063%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 33.1063%; height: 21.75px;\\">&nbsp;</td>\\n</tr>\\n<tr style=\\"height: 21.75px;\\">\\n<td style=\\"width: 33.1063%; height: 21.75px;\\">&nbsp;</td>\\n<td style=\\"width: 33.1063%; height: 21.75px;\\">热热</td>\\n<td style=\\"width: 33.1063%; height: 21.75px;\\">&nbsp;</td>\\n</tr>\\n</tbody>\\n</table>\\n<p>&nbsp;</p>\\n<table style=\\"border-collapse: collapse; width: 100.034%;\\" border=\\"1\\">\\n<tbody>\\n<tr>\\n<td style=\\"width: 33.09%;\\">热热</td>\\n<td style=\\"width: 33.09%;\\">&nbsp;</td>\\n<td style=\\"width: 33.0917%;\\">&nbsp;</td>\\n</tr>\\n</tbody>\\n</table>'
    let string = data
    if (string) {
        let imgContents = [];
        let tableContents = [];
        let newStr = string;
        let imgStart = newStr.indexOf("<img");
        while (imgStart !== -1) {
            let imgEnd = newStr.indexOf(">", imgStart);
            if (imgEnd === -1) {
                break;
            }
            imgContents.push(newStr.substring(imgStart, imgEnd + 1));
            newStr = newStr.substring(0, imgStart) + newStr.substring(imgEnd + 1);
            imgStart = newStr.indexOf("<img");
        }
        let tableStart = newStr.indexOf("<table");
        while (tableStart !== -1) {
            let tableEnd = newStr.indexOf("</table>");
            if (tableEnd === -1) {
                break;
            }
            tableContents.push(newStr.substring(tableStart, tableEnd + 8));
            newStr = newStr.substring(0, tableStart) + newStr.substring(tableEnd + 8);
            tableStart = newStr.indexOf("<table");
        }
        let text = newStr.replace(/<[^>]+>/g, '');
        // text = unescape(text)
        text = text.replace(/\\n/g, '');
        text = text.replace(/&nbsp;/g, ' ');
        text = text.replace(/&lt;/g, '<');
        text = text.replace(/&gt;/g, '>');
        text = text.replace(/&amp;/g, '&');

        res['text'] = text
        res['imgContents'] = imgContents
        res['tableContents'] = tableContents
    } else {
        res['text'] = ''
        res['imgContents'] = null
        res['tableContents'] = null
    }
    return res

}
export {html2string}