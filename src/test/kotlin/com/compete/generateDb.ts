let main = (args: Object) => {
    for (const key in args) {
        console.log(key)
    }
}

let db = {
    "searchValue": null,
    "createBy": null,
    "createTime": null,
    "updateBy": null,
    "updateTime": null,
    "remark": null,
    "params": {},
    "id": 4,
    "name": "衣食住行",
    "sort": 1,
    "imgUrl": "/dev-api/profile/upload/image/2022/02/24/aed84 b53-f416-4af1-be5e-e70a9ddb8027.png"
};
main(db);