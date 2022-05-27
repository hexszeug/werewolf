import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';

const root = ReactDOM.createRoot(
	document.getElementById('root') as HTMLElement,
);
root.render(<App />);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();

export const ObjectMap = <T extends object>(
	obj: T,
	callbackfn: (
		value: [string, any],
		index: number,
		array: [string, any][],
	) => [keyof T, any],
) => {
	return Object.fromEntries(Object.entries(obj).map(callbackfn));
};

export const ObjectValuesMap = <T extends object>(
	obj: T,
	callbackfn: (value: any, index: number, array: any[]) => any,
) => {
	return Object.values(obj).map(callbackfn);
};
