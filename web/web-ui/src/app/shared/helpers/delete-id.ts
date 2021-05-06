export function deleteId(obj) {
    const objToReturn = obj.map(el => {
        const toReturn = {...el};
        if ('id' in toReturn) {
          delete toReturn.id;
        }
        return toReturn;
      });
    return objToReturn;
}
