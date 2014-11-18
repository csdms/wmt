import json

from flask import Response


def as_resource(resp):
    return Response(json.dumps(resp, sort_keys=True, indent=2,
                               separators=(',', ': ')),
                   mimetype='application/x-resource+json; charset=utf-8')


def as_collection(resp):
    return Response(json.dumps(resp, sort_keys=True, indent=2,
                               separators=(',', ': ')),
                   mimetype='application/x-collection+json; charset=utf-8')


def jsonify_collection(models):
    collection = []
    for model in models:
        collection.append(model.to_resource())
    return Response(json.dumps(collection, sort_keys=True, indent=2,
                               separators=(',', ': ')),
                    mimetype='application/x-collection+json; charset=utf-8')
