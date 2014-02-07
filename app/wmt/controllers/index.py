import web


from ..render import render
from ..models import models, hosts
from ..session import get_session


class Index(object):
    def GET(self):
        return render.index(models.get_models(), hosts.get_hosts(), get_session())


class Help(object):
    """
    Get help about CMT or a specific command. To get help for a command,
    just add the command name to the end of the URL. For instance, to get
    help for the *show* command,

    * https://csdms.colorado.edu/wmt/help/show
    """
    def GET(self, *args):
        from docutils.core import publish_parts
        if len(args) == 1:
            documentation = globals()[args[0].title()].__doc__
            html = publish_parts(source=documentation, writer_name='html')
            return html['html_body']
        return render.help()
