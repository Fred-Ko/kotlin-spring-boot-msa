{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Project",
  "type": "object",
  "properties": {
    "name": {
      "type": "string"
    },
    "language": {
      "type": ["string", "null"]
    }
  },
  "required": ["name"]
}