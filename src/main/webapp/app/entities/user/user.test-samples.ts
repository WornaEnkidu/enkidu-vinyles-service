import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 2750,
  login: 'W@',
};

export const sampleWithPartialData: IUser = {
  id: 30138,
  login: 'CEpCF@3u\\tGtxq9j\\fsPlU\\]MD5Q\\J-I-l',
};

export const sampleWithFullData: IUser = {
  id: 20240,
  login: 'im@hOx6\\Kq3Tyf\\nQkpOMI',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
