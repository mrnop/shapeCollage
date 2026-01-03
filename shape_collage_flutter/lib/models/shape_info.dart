class ShapeInfo {
  static const String TYPE_BUNDLE = "bundle";
  static const String TYPE_CUSTOM = "custom";

  int? id;
  String? info;
  String? folder;
  String? path;
  String? type;
  DateTime? created;

  ShapeInfo({
    this.id,
    this.info,
    this.folder,
    this.path,
    this.type = TYPE_BUNDLE,
    this.created,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'info': info,
      'folder': folder,
      'path': path,
      'type': type,
      'created': created?.millisecondsSinceEpoch,
    };
  }

  factory ShapeInfo.fromMap(Map<String, dynamic> map) {
    return ShapeInfo(
      id: map['id'],
      info: map['info'],
      folder: map['folder'],
      path: map['path'],
      type: map['type'],
      created: map['created'] != null
          ? DateTime.fromMillisecondsSinceEpoch(map['created'])
          : null,
    );
  }
}
