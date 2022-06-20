import './Narrator.scss';
import { PhaseType } from '../Game';

export const Narrator = (props: { phase: PhaseType }) => {
	return (
		<div className='Narrator'>
			<h3>Current Phase:</h3>
			<h1>{props.phase}</h1>
		</div>
	);
};

export default Narrator;
