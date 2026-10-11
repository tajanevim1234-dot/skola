const menuToggle = document.querySelector('.menu-toggle');

if (menuToggle) {
    menuToggle.hidden = false;
    menuToggle.setAttribute('aria-expanded', 'false');

    menuToggle.addEventListener('click', () => {
        const isExpanded = menuToggle.getAttribute('aria-expanded') === 'true';
        menuToggle.setAttribute('aria-expanded', String(!isExpanded));
    });

    document.querySelector('.site-nav').addEventListener('keydown', (event) => {
        if (event.key === 'Escape' && menuToggle.getAttribute('aria-expanded') === 'true') {
            menuToggle.setAttribute('aria-expanded', 'false');
            menuToggle.focus();
        }
    });
}