/**
 * Register an event at the document for the specified selector,
 * so events are still catched after DOM changes.
 */
function addEvent(eventType, selector, handler) {
    document.addEventListener(eventType, function(event) {
        if (event.target.matches(selector + ', ' + selector + ' *')) {
            handler.apply(event.target.closest(selector), arguments);
        }
    });
}

addEvent('submit', '.js-submit-confirm', function(event) {
    if (!confirm(this.getAttribute('data-confirm-message'))) {
        event.preventDefault();
        return false;
    }
    return true;
});
