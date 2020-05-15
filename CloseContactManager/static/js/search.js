function startSearch(searchId, searchPanelId) {
    $("#" + searchId).hide();
    $("#" + searchPanelId).show();
}

function closeSearch(searchId, searchPanelId) {
    $("#" + searchId).show();
    $("#" + searchPanelId).hide();
}

function onSearch(keyword,listData,tableId){
    var newListData =
}