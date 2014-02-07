#! /usr/bin/env python


class Port(object):
    """
    A Component Modeling Tool port.

    >>> port = Port('port_name@component_name')
    >>> port.component
    'component_name'
    >>> port.port
    'port_name'

    >>> port = Port('port_name', 'component_name')
    >>> port.component
    'component_name'
    >>> port.port
    'port_name'
    """
    def __init__(self, *args):
        try:
            (port, component) = args
        except ValueError:
            (port, component) = args[0].split('@')

        (self._port, self._component) = (port, component)

    @property
    def port(self):
        return self._port

    @property
    def component(self):
        return self._component

    def __str__(self):
        return '@'.join([self.port, self.component])

    def __repr__(self):
        return 'Port(%s, %s)' % (self.port, self.component)


if __name__ == '__main__':
    import doctest
    doctest.testmod()
