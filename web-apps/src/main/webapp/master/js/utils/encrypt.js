function d(t) {
    var e, i = [];
    for (i[(t.length >> 2) - 1] = void 0, e = 0; e < i.length; e += 1) i[e] = 0;
    var s = 8 * t.length;
    for (e = 0; e < s; e += 8) i[e >> 5] |= (255 & t.charCodeAt(e / 8)) << e % 32;
    return i
};