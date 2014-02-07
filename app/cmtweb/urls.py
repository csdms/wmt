from . import controllers


URLS = (
    '/', 'Index',
    '/help', 'Help',
    '/help/(.*)', 'Help',

    '/login', 'Login',
    '/logout', 'Logout',

    '/new', 'New',
    '/delete/(\d+)', 'Delete',
    '/edit/(\d+)', 'Edit',
    '/view/(\d+)', 'View',
    '/export/(\d+)', 'Export',
    '/view', 'Show',
    '/show', 'Show',
    '/convert', 'Convert',
)
