from ..decorators import as_json


class Root(object):
    @as_json
    def GET(self):
        return {
            "users_url": "/api/users",
            "user_url": "/api/users/{user}",
            "components_url": "/api/components",
            "component_url": "/api/components/{component}",
            "parameters_url": "/api/components/{component}/params",
            "parameter_url": "/api/components/{component}/params/{parameter}",
            "files_url": "/api/components/{component}/files",
            "file_url": "/api/components/{component}/files/{file}",
            "inputs_url": "/api/components/{component}/inputs",
            "outputs_url": "/api/components/{component}/outputs",
            "tags_url": "/api/tags",
            "tag_url": "/api/tags/{tag}",
        }
