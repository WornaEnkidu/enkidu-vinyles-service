import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '338dc58d-4a1d-441b-9384-65a788150c7b',
};

export const sampleWithPartialData: IAuthority = {
  name: '94ab8d49-6887-4782-a9e3-50b28c35c868',
};

export const sampleWithFullData: IAuthority = {
  name: '5c4ff283-8585-424b-be7b-f4706ef6dc87',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
