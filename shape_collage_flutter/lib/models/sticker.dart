class Sticker {
  int? id;
  String? refNo;
  String? stickerName;
  String? remoteUrl;
  String? file;
  bool? active;
  DateTime? created;

  Sticker({
    this.id,
    this.refNo,
    this.stickerName,
    this.remoteUrl,
    this.file,
    this.active,
    this.created,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'refNo': refNo,
      'stickerName': stickerName,
      'remoteUrl': remoteUrl,
      'file': file,
      'active': active == true ? 1 : 0,
      'created': created?.millisecondsSinceEpoch,
    };
  }

  factory Sticker.fromMap(Map<String, dynamic> map) {
    return Sticker(
      id: map['id'],
      refNo: map['refNo'],
      stickerName: map['stickerName'],
      remoteUrl: map['remoteUrl'],
      file: map['file'],
      active: map['active'] == 1,
      created: map['created'] != null
          ? DateTime.fromMillisecondsSinceEpoch(map['created'])
          : null,
    );
  }
}
